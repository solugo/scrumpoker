import {BehaviorSubject, queueScheduler} from "rxjs";
import {Player, Room, Session} from "./Session";
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
            this.socket.onopen = () => {
                this.sessionSubject.next({session: new Session()})
            }
            this.socket.onclose = () => {
                this.sessionSubject.next({})
            }
            this.socket.onmessage = (message) => {
                this.handle(JSON.parse(message.data))
            }
            return () => {
                this.socket?.close()
                this.socket = undefined
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
    }

    handle(event: SessionEvent) {
        let session = this.sessionSubject.value.session

        if (session != null) {
            switch (event.type) {
                case 'sessionStarted': {
                    session.playerId = event.playerId
                    break
                }
                case 'playerJoinedRoom': {
                    const room = session.rooms[event.roomId] = session.rooms[event.roomId] ?? new Room()
                    room.players[event.playerId] = room.players[event.playerId] ?? new Player()
                    break
                }
                case 'playerLeftRoom': {
                    delete session.rooms?.[event.roomId]?.players?.[event.playerId]
                    break
                }
                case 'playerInfoChanged': {
                    const room = session.rooms[event.roomId] = session.rooms[event.roomId] ?? new Room()
                    const player = room.players[event.playerId] = room.players[event.playerId] ?? new Player()
                    player.name = event.name
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
                    Object.entries(event.selections).forEach(([playerId, selection]) => {
                        const player = room.players[playerId] = room.players[playerId] ?? {}
                        player.selection = selection
                    })

                    room.state.min = event.min
                    room.state.avg = event.avg
                    room.state.max = event.max

                    break
                }
            }
        }

        this.sessionSubject.next({session: session})
    }
}