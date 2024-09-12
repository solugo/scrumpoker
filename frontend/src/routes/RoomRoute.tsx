import {useParams} from "react-router-dom";
import RoomDisplayView from "../components/RoomDisplayView";
import RoomJoinView from "../components/RoomJoinView";
import {useSessionApi} from "../hooks/UseSessionApi.ts";


export default function RoomRoute() {

    const api = useSessionApi()

    const {roomId} = useParams<{ roomId: string }>()

    const room = api.state.rooms[roomId ?? ''] ?? undefined
    const participantId = api.state.participantId
    const participant = room?.participants?.[participantId ?? ''] ?? undefined

    if (roomId) {
        if (room && participantId && participant) {
            return (
                <RoomDisplayView
                    api={api}
                    roomId={roomId}
                    room={room}
                    participantId={participantId}
                    participant={participant}
                />
            )
        } else {
            return (
                <RoomJoinView
                    api={api}
                    roomId={roomId}
                />
            )
        }
    } else {
        return (
            <div>Error</div>
        )
    }
}