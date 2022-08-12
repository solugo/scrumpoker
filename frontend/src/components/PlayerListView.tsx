import React, {HTMLProps} from "react";
import PlayerView from "./PlayerView";
import {styled} from "@mui/material";
import {Player} from "../logic/Session";

export interface PlayerListViewProps extends HTMLProps<HTMLElement>{
    players: [string, Player][]
}

export default styled(
    (props: PlayerListViewProps) => {
        return (
            <div className={props.className}>
                {props.players.map(([id, player]) => (<PlayerView key={id} id={id} player={player}/>))}
            </div>
        )
    }, {
        name: 'PlayerListView'
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