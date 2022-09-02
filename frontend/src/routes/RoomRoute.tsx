import React from "react";
import {useParams} from "react-router-dom";
import {useSession} from "../components/SessionScope";
import RoomDisplayView from "../components/RoomDisplayView";
import RoomJoinView from "../components/RoomJoinView";

const RoomRoute = () => {
    const {roomId} = useParams<{ roomId: string }>()

    const session = useSession()
    const room = roomId ? session?.rooms?.[roomId] : undefined
    const participantId = session?.participantId
    const participant = participantId ? room?.participants?.[participantId] : undefined

    if (roomId) {
        if (room && participantId && participant) {
            return (
                <RoomDisplayView roomId={roomId} room={room} participantId={participantId} participant={participant}/>
            )
        } else {
            return (
                <RoomJoinView roomId={roomId}/>
            )
        }
    } else {
        return (
            <div>Error</div>
        )
    }
}

export default RoomRoute