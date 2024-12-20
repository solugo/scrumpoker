import {useEffect, useMemo, useState} from "react";
import {Participant, Room, SessionState} from "../logic/SessionState.ts";
import useWebSocket, {ReadyState} from "react-use-websocket";
import {SessionRequest} from "../logic/SessionRequest.ts";
import {SessionEvent} from "../logic/SessionEvent.ts";
import {SessionApi} from "../logic/SessionApi.ts";


export function useSessionApi(): SessionApi {
    const [holder, updateHolder] = useState<[SessionState]>(() => [new SessionState()])

    const url = useMemo(() => `${window.location.toString().split('#')[0]}api`, [])
    const {lastMessage, sendMessage, readyState} = useWebSocket(url, {shouldReconnect: () => true, retryOnError: true})

    useEffect(() => {
        if (readyState === ReadyState.OPEN) updateHolder([new SessionState()])
    }, [updateHolder, readyState]);

    useEffect(() => {
        if (lastMessage) updateHolder(prev => {
            const event: SessionEvent = JSON.parse(lastMessage.data.toString())
            const [state] = prev
            switch (event.type) {
                case 'error': {
                    console.error("Error from backend", event.message)
                    break
                }
                case 'sessionStarted': {
                    state.participantId = event.participantId
                    break
                }
                case 'participantJoinedRoom': {
                    const room = state.rooms[event.roomId] = state.rooms[event.roomId] ?? new Room()
                    const participant = room.participants[event.participantId] = room.participants[event.participantId] ?? new Participant()
                    participant.name = event.name
                    participant.role = event.role
                    break
                }
                case 'participantLeftRoom': {
                    delete state.rooms?.[event.roomId]?.participants?.[event.participantId]
                    break
                }
                case 'roomInfoChanged': {
                    const room = state.rooms[event.roomId] = state.rooms[event.roomId] ?? new Room()
                    room.name = event.name
                    room.options = event.options
                    break
                }
                case 'roomSelectionChanged': {
                    const room = state.rooms[event.roomId] = state.rooms[event.roomId] ?? new Room()
                    room.visible = event.visible
                    Object.entries(event.selections).forEach(([participantId, selection]) => {
                        const participant = room.participants[participantId] = room.participants[participantId] ?? {}
                        participant.selection = selection
                    })

                    break
                }
                case 'participantKicked': {
                    const room = state.rooms[event.roomId] = state.rooms[event.roomId] ?? new Room()
                    delete room.participants?.[event.participantId]
                }
            }
            return [state]
        })
    }, [updateHolder, lastMessage])

    return {
        state: holder[0],
        send: (event: SessionRequest) => sendMessage(JSON.stringify(event)),
    }
}
