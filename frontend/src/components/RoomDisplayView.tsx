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

    function kickParticipant(participantId: string) {
        api?.kickParticipant(roomId, participantId)
    }

    let statTotal = 0
    const statValues: { [key: string]: number } = {}

    if (room.visible) {
        Object.values(participants).forEach((participant) => {
            if (participant.selection) {
                statTotal += 1
            }
        })

        Object.values(participants).forEach((participant) => {
            if (participant.selection) {
                const selection = participant.selection || ""
                statValues[selection] = (statValues[selection] || 0) + 1
            }
        })
    }

    const statEntries = Object.entries(statValues).sort((a, b) => b[1] - a[1])

    return (
        <div className={props.className}>
            <div className="participantList">
                {participantEntries.map(
                    ([participantId, participant]) => (
                        <div className="participant" key={participantId}>
                            <GameCard
                                type={participant.role === "PLAYER" ? "VIEW" : "DISABLED"}
                                selected={participant.selection !== null}
                                title={participant.name || "🕶️"}
                                value={participant.role === "SPECTATOR" ? "👁️" : (room.visible ? participant.selection : "")}
                                onClickCancel={participantId !== props.participantId ? () => kickParticipant(participantId): undefined}
                            />
                        </div>
                    )
                )}
            </div>
            <div className="statsList">
                {statEntries.map(
                    ([title, statValue]) => {
                        const percentage = 100.0 * statValue / statTotal
                        return (
                            <div key={title} className="statContainer">
                                <div className="statEntry" key={title}>
                                    <div className="statSpace" style={{height: `${100 - percentage}%`}}></div>
                                </div>
                                <div className="statNumber">{title}</div>
                            </div>
                        )
                    }
                )}
            </div>
            <div className="actionList">
                <Button color="success" variant="contained" className="action" onClick={reveal}>
                    {(room.visible) ? "Hide" : "Reveal"}
                </Button>
                <Button color="warning" variant="contained" className="action" onClick={restart}>Restart</Button>
            </div>
            <div className="optionList">
                {options.map(
                    ([selection, value]) => {
                        return (
                            <div className="option" key={selection}>
                                <GameCard
                                    type="SELECT"
                                    selected={props.participant.selection === selection}
                                    value={selection}
                                    onClick={() => updateSelection(selection)}
                                />
                            </div>
                        )
                    }
                )}
            </div>
        </div>
    )
}

export default styled(RoomDisplayView)((props) => (
    {
        display: "grid",
        gap: "1rem",
        gridTemplateRows: '1fr auto auto auto',
        padding: "1rem",
        ".actionList": {
            display: "flex",
            flexWrap: "wrap",
            gap: "1rem",
            justifyContent: "center",
            "button": {
                width: "8rem"
            },
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
        ".statsList": {
            display: "flex",
            flexGrow: "content",
            gap: "1rem",
            height: "auto",
            justifyContent: "center",
            alignItems: "stretch",
        },
        ".statEntry": {
            aspectRatio: "2 / 4",
            border: "solid 1px",
            height: "3rem",
            backgroundColor: props.theme.palette.secondary.main,
            borderColor: props.theme.palette.secondary.dark,
        },
        ".statSpace": {
            marginTop: "auto",
            backgroundColor: props.theme.palette.background.default,
        },
        ".statNumber": {
            paddingTop: "0.5rem",
            textAlign: "center",
        },
    }
))