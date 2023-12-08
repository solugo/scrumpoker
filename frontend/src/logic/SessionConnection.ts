import {BehaviorSubject, queueScheduler} from "rxjs";
import {Participant, Room, Session} from "./Session";
import {SessionEvent} from "./SessionEvent";
import {SessionRequest} from "./SessionRequest";

export class SessionConnection {

    private sessionSubject = new BehaviorSubject<{ session?: Session }>({})
    private socket: WebSocket | undefined = undefined

    public readonly session$ = this.sessionSubject.asObservable()

    open(): () => void {
        if (this.socket === undefined) {
            const location = window.location
            const protocol = (location.protocol === "https:") ? "wss:" : "ws:"
            this.socket = new WebSocket(`${protocol}//${location.host}/api`)
            this.socket.onopen = (event) => {
                this.sessionSubject.next({session: new Session()})
            }
            this.socket.onclose = (event) => {
                this.sessionSubject.next({})
                setTimeout(() => this.open(), 1000)
            }
            this.socket.onerror = (event) => {
                this.sessionSubject.next({})
                setTimeout(() => this.open(), 1000)
            }
            this.socket.onmessage = (message) => {
                this.handle(JSON.parse(message.data))
            }
            return () => {
                this.close()
            }
        } else {
            return () => {
            }
        }
    }

    send(request: SessionRequest) {
        const connection = this
        queueScheduler.schedule(function () {
            if (connection.socket?.readyState == WebSocket.OPEN) {
                connection.socket.send(JSON.stringify(request))
            } else {
                this.schedule(null, 100)
            }
        })
    }

    close() {
        this.socket?.close()
        this.socket = undefined
    }

    handle(event: SessionEvent) {
        let session = this.sessionSubject.value.session

        if (session != null) {
            switch (event.type) {
                case 'error': {
                    console.error("Error from backend", event.message)
                    break
                }
                case 'sessionStarted': {
                    session.participantId = event.participantId
                    break
                }
                case 'participantJoinedRoom': {
                    const room = session.rooms[event.roomId] = session.rooms[event.roomId] ?? new Room()
                    const participant = room.participants[event.participantId] = room.participants[event.participantId] ?? new Participant()
                    participant.name = event.name
                    participant.role = event.role
                    break
                }
                case 'participantLeftRoom': {
                    delete session.rooms?.[event.roomId]?.participants?.[event.participantId]
                    break
                }
                case 'roomInfoChanged': {
                    const room = session.rooms[event.roomId] = session.rooms[event.roomId] ?? new Room()
                    room.name = event.name
                    room.options = event.options
                    break
                }
                case 'roomSelectionChanged': {
                    const room = session.rooms[event.roomId] = session.rooms[event.roomId] ?? new Room()
                    room.visible = event.visible
                    Object.entries(event.selections).forEach(([participantId, selection]) => {
                        const participant = room.participants[participantId] = room.participants[participantId] ?? {}
                        participant.selection = selection
                    })

                    room.state.min = event.min
                    room.state.avg = event.avg
                    room.state.max = event.max

                    break
                }
                case 'participantKicked': {
                    const room = session.rooms[event.roomId] =session.rooms[event.roomId] ?? new Room()
                    delete room.participants?.[event.participantId]
                }
            }
        }

        this.sessionSubject.next({session: session})
    }
}
