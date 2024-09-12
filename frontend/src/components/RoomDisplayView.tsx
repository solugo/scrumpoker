import {Participant, Room} from "../logic/SessionState.ts";
import GameCard from "./GameCard";
import {Button, styled} from "@mui/material";
import {SessionApi} from "../logic/SessionApi.ts";

interface RoomDisplayViewProps {
    api: SessionApi,
    participantId: string
    participant: Participant
    roomId: string
    room: Room
    className?: string
}

const RoomDisplayView = (props: RoomDisplayViewProps) => {
    const roomId = props.roomId
    const room = props.room
    const participants = room.participants
    const participantEntries = Object.entries(participants).sort((a, b) => a[0].localeCompare(b[0]))
    const options = Object.entries(props.room?.options ?? {}).sort((a, b) => a[1] - b[1])

    function updateSelection(value: string) {
        props.api.send({
            type: 'updateSelection',
            selection: props.participant.selection !== value ? value : null,
            roomId,
        })
    }

    function reveal() {
        props.api.send({
            type: 'revealRoom',
            visible: !room.visible,
            roomId,
        })
    }

    function restart() {
        props.api.send({
            type: 'resetRoom',
            roomId,
        })
    }

    function kickParticipant(participantId: string) {
        props.api.send({
            type: 'kickParticipant',
            roomId,
            participantId,
        })
    }

    const selectionValues: { [key: string]: number } = {}
    const selectionIncidence: { [key: string]: number } = {}

    let selectionSum = 0
    let selectionCount = 0
    const incidents: { selection: string, value: number }[] = []

    if (room.visible) {
        options.forEach(([selection, value]) => {
            selectionValues[selection] = value
        })
        Object.values(participants).forEach((participant) => {
            const selection = participant.selection
            const value = selectionValues[selection ?? ""]
            if (selection) {
                selectionIncidence[selection] = (selectionIncidence[selection] ?? 0) + 1
            }
            if (selection && value) {
                selectionCount += 1
                selectionSum += value
                incidents.push({selection, value})
            }
        })
    }

    const selectionIncidentEntries = Object.entries(selectionIncidence).sort((a, b) => b[1] - a[1])
    const selectionValueEntries = incidents.sort((a, b) => a.value - b.value)

    const valueMin = selectionCount === 0 ? 0 : (selectionValueEntries[0]?.value ?? 0)
    const valueMax = selectionCount === 0 ? 0 : (selectionValueEntries[selectionValueEntries.length - 1]?.value ?? 0)
    const valueAvg = selectionCount === 0 ? 0 : (Math.round(100 * selectionSum / selectionCount) / 100)
    const valueMed = selectionCount === 0 ? 0 : (selectionValueEntries[Math.floor(selectionValueEntries.length / 2)]?.value ?? 0)
    const valueDev = selectionCount === 0 ? 0 : (valueMax - valueMin)

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
                                onClickCancel={participantId !== props.participantId ? () => kickParticipant(participantId) : undefined}
                            />
                        </div>
                    )
                )}
            </div>
            <div className="statsList">
                {!room.visible ? null : (
                    <>
                        {selectionIncidentEntries.map(
                            ([title, count]) => {
                                const percentage = 100.0 * count / selectionCount
                                return (
                                    <div key={title} className="statContainer">
                                        <div className="statEntry">
                                            <div className="statSpace" style={{height: `${100 - percentage}%`}}></div>
                                        </div>
                                        <div className="statNumber">{title}</div>
                                    </div>
                                )
                            }
                        )}
                    </>
                )}
            </div>
            <div className="statsDetails">
                {!room.visible ? null : (
                    valueDev === 0 ? (
                        <>
                            <div className="statsDetailLabel" style={{gridColumn: 1}}>RESULT</div>
                            <div className="statsDetailValue" style={{gridColumn: 1}}>AGREEMENT</div>
                        </>
                    ) : (
                        <>
                            <div className="statsDetailLabel" style={{gridColumn: 1}}>MIN
                            </div>
                            <div className="statsDetailValue" style={{gridColumn: 1}}>{valueMin}</div>
                            <div className="statsDetailLabel" style={{gridColumn: 2}}>AVG</div>
                            <div className="statsDetailValue" style={{gridColumn: 2}}>{valueAvg}</div>
                            <div className="statsDetailLabel" style={{gridColumn: 3}}>DEV</div>
                            <div className="statsDetailValue" style={{gridColumn: 3}}>{valueDev}</div>
                            <div className="statsDetailLabel" style={{gridColumn: 4}}>MED</div>
                            <div className="statsDetailValue" style={{gridColumn: 4}}>{valueMed}</div>
                            <div className="statsDetailLabel" style={{gridColumn: 5}}>MAX</div>
                            <div className="statsDetailValue" style={{gridColumn: 5}}>{valueMax}</div>
                        </>
                    )
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
                    ([selection]) => {
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
        ".statsDetails": {
            display: "grid",
            gridTemplateRows: 'auto',
            columnGap: "1rem",
            height: "auto",
            margin: "auto",
            justifyItems: "center",
            alignItems: "center",
        },
        ".statsDetailLabel": {
            gridRow: 1,
            userSelect: "none",
            fontSize: "75%",
            color: props.theme.palette.secondary.dark,
        },
        ".statsDetailValue": {
            gridRow: 2,
            fontWeight: "bold",
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
            textAlign: "center",
            backgroundColor: props.theme.palette.background.default,
        },
        ".statNumber": {
            paddingTop: "0.5rem",
            fontWeight: "bold",
            textAlign: "center",
        },
    }
))