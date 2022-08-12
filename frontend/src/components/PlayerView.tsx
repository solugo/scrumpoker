import {Card, styled} from "@mui/material";
import {HTMLProps} from "react";
import {Player} from "../logic/Session";

export interface PlayerViewProps extends HTMLProps<HTMLElement> {
    id: string,
    player: Player
}

export default styled(
    (props: PlayerViewProps) => {
        return (
            <div className={props.className}>
                <div className="header">
                    {props.player.name ?? props.id}
                </div>
                <Card className="card">
                    {props.player.selection}
                </Card>
            </div>
        )
    }, {
        name: 'PlayerView',
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
            background-color: ${props.player.selection ? props.theme.palette.primary.main : props.theme.palette.background.paper};
        }
    `
)