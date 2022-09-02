import {Card, styled} from "@mui/material";
import {HTMLProps} from "react";
import {Participant} from "../logic/Session";

export interface ParticipantViewProps extends HTMLProps<HTMLElement> {
    id: string,
    participant: Participant
}

export default styled(
    (props: ParticipantViewProps) => {
        return (
            <div className={props.className}>
                <div className="header">
                    {props.participant.name ?? props.id}
                </div>
                <Card className="card">
                    {props.participant.selection}
                </Card>
            </div>
        )
    }, {
        name: 'ParticipantView',
    }
)(
    (props) => `
        min-width: 400px;
        padding: ${props.theme.spacing(2)};
        text-align: center;
        
        .header {
            padding: ${props.theme.spacing(2)};
            font-size: ${props.theme.typography.h6.fontSize};
            font-weight: ${props.theme.typography.h6.fontWeight}
        }
        
        .card {
            padding: ${props.theme.spacing(2)};
            margin: auto;
            width: 80px;
            height: 120px;
            background-color: ${props.participant.selection ? props.theme.palette.primary.main : props.theme.palette.background.paper};
        }
    `
)