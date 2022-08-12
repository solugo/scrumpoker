import React from "react";
import {Player, Room} from "../logic/Session";
import GameCard from "./GameCard";
import {Button, styled} from "@mui/material";
import {useSession, useSessionApi} from "./SessionScope";

interface RoomViewProps {
    playerId: string
    roomId: string
    room: Room
    className?: string
}

const RoomView = (props: RoomViewProps) => {
    const api = useSessionApi()

    const roomId = props.roomId
    const room = props.room
    const players = room.players
    const self = players[props.playerId]
    const playerEntries = Object.entries(players).sort((a,b) => a[0].localeCompare(b[0]))
    const options = Object.entries(props.room?.options ?? {})

    function updateSelection(value: string) {
        api?.changeSelection(roomId, self.selection !== value ? value : null)
    }

    function reveal() {
        api?.revealRoom(roomId, !room.visible)
    }

    function restart() {
        api?.resetRoom(roomId)
    }

    return (
        <div className={props.className}>
            <div className="playerList">
                {playerEntries.map(
                    ([playerId, player]) => (
                        <div className="player" key={playerId}>
                            <GameCard selected={player.selection !== null} title={player.name ?? `Player ${playerId}`}
                                      value={room.visible ? player.selection : ""}/>
                        </div>
                    )
                )}
            </div>
            <div className="actionList">
                <Button variant="contained" className="action"
                        onClick={reveal}>{(room.visible) ? "Hide" : "Reveal"}</Button>
                <Button variant="contained" className="action" onClick={restart}>Restart</Button>
            </div>
            <div className="optionList">
                {options.map(
                    ([selection, value]) => (
                        <div className="option" key={selection}>
                            <GameCard selected={self.selection === selection} value={selection}
                                      onClick={() => updateSelection(selection)}/>
                        </div>
                    )
                )}
            </div>
        </div>
    )
}

export default styled(RoomView)(
    (props) => (
        {
            display: "grid",
            gap: "2rem",
            gridTemplateRows: 'auto 2rem 9rem',
            padding: "1rem",

            ".actionList": {
                display: "flex",
                flexWrap: "wrap",
                gap: "1rem",
                justifyContent: "center",
            },

            ".optionList": {
                display: "flex",
                flexWrap: "wrap",
                gap: "1rem",
                justifyContent: "center",
            },

            ".option": {
                display: "grid",
                width: "4rem",
                height: "6rem",
            },

            ".playerList": {
                display: "flex",
                flexWrap: "wrap",
                gap: "1rem",
                justifyContent: "center",
            },

            ".player": {
                display: "grid",
                width: "8rem",
                height: "12rem",
            },

        }
    )
)