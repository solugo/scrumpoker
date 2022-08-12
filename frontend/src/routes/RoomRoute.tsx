import React, {useEffect} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {useSession, useSessionApi} from "../components/SessionScope";
import RoomView from "../components/RoomView";

export default function RoomRoute() {
    const {roomId} = useParams<{ roomId: string }>()

    const session = useSession()
    const api = useSessionApi()
    const playerId = session?.playerId
    const room = roomId ? session?.rooms?.[roomId] : undefined

    useEffect(() => {
        if (roomId) {
            api?.joinRoom(roomId)
        }
    })

    if (playerId && roomId && room) {
        return (
            <RoomView playerId={playerId} roomId={roomId} room={room}/>
        )
    } else {
        return (
            <div/>
        )
    }
}