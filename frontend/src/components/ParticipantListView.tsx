import React, {HTMLProps} from "react";
import ParticipantView from "./ParticipantView";
import {styled} from "@mui/material";
import {Participant} from "../logic/Session";

export interface ParticipantListViewProps extends HTMLProps<HTMLElement>{
    participants: [string, Participant][]
}

export default styled(
    (props: ParticipantListViewProps) => {
        return (
            <div className={props.className}>
                {props.participants.map(([id, participant]) => (<ParticipantView key={id} id={id} participant={participant}/>))}
            </div>
        )
    }, {
        name: 'ParticipantListView'
    }
)(
    (props) => `
        display: flex;
        padding: ${props.theme.spacing(2)};
        flex-wrap: wrap;
        gap: ${props.theme.spacing(2)};
        justify-content: center;
    `
)