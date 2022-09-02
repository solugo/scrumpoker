import React from "react";
import {Participant, Room} from "../logic/Session";
import GameCard from "./GameCard";
import {Button, styled} from "@mui/material";
import {useSessionApi} from "./SessionScope";

interface RoomDisplayViewProps {
    participantId: string
    participant: Participant
    roomId: string
    room: Room
    className?: string
}

const RoomDisplayView = (props: RoomDisplayViewProps) => {
    const api = useSessionApi()

    const roomId = props.roomId
    const room = props.room
    const participants = room.participants
    const participantEntries = Object.entries(participants).sort((a, b) => a[0].localeCompare(b[0]))
    const options = Object.entries(props.room?.options ?? {}).sort((a, b) => a[1] - b[1])

    function updateSelection(value: string) {
        api?.changeSelection(roomId, props.participant.selection !== value ? value : null)
    }

    function reveal() {
        api?.revealRoom(roomId, !room.visible)
    }

    function restart() {
        api?.resetRoom(roomId)
    }

    return (
        <div className={props.className}>
            <div className="participantList">
                {participantEntries.map(
                    ([participantId, participant]) => (
                        <div className="participant" key={participantId}>
                            <GameCard
                                type={participant.mode === "PLAYER" ? "VIEW" : "DISABLED"}
                                selected={participant.selection !== null}
                                title={participant.name || "🕶️"}
                                value={participant.mode === "SPECTATOR" ? "👁️" : (room.visible ? participant.selection : "")}
                            />
                        </div>
                    )
                )}
            </div>
            <div className="actionList">
                <Button
                    variant="contained"
                    className="action"
                    onClick={reveal}
                >
                    {(room.visible) ? "Hide" : "Reveal"}
                </Button>
                <Button variant="contained" className="action" onClick={restart}>Restart</Button>
            </div>
            <div className="optionList">
                {options.map(
                    ([selection, value]) => (
                        <div className="option" key={selection}>
                            <GameCard
                                type="SELECT"
                                selected={props.participant.selection === selection}
                                value={selection}
                                onClick={() => updateSelection(selection)}
                            />
                        </div>
                    )
                )}
            </div>
        </div>
    )
}

export default styled(RoomDisplayView)(
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

        ".participantList": {
            display: "flex",
            flexWrap: "wrap",
            gap: "1rem",
            justifyContent: "center",
        },

        ".participant": {
            display: "grid",
            width: "8rem",
            height: "12rem",
        },

    }
)