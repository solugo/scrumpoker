import React from "react";
import {styled} from "@mui/material";
import classNames from "classnames";

interface GameCardProps {
    className?: string
    selected: boolean
    title?: string | undefined
    value?: string | null
    onClick?: () => void
}

const GameCard = (props: GameCardProps) => {
    return (
        <div className={props.className}>
            <div className="title">
                {props.title}
            </div>
            <div className={classNames("card", {"card-selected": props.selected})} onClick={() => props.onClick?.()}>
                <span>{props.value}</span>
            </div>
        </div>
    )
}

export default styled(GameCard)((props) => (
    {
        display: "grid",
        gridTemplateRows: 'min-content auto',
        ".title": {
            padding: "0.2rem",
            textAlign: "center",
            whiteSpace: "nowrap",
            overflow: "hidden",
            textOverflow: "ellipsis",
        },
        ".card": {
            position: "relative",
            display: "grid",
            alignItems: "center",
            justifyContent: "center",
            borderRadius: "0.4rem",
            fontSize: "2rem",
            color: props.theme.palette.primary.contrastText,
            backgroundColor: props.theme.palette.grey.A700,
        },
        ".card:hover": {
            position: "relative",
            display: "grid",
            alignItems: "center",
            justifyContent: "center",
            borderRadius: "0.4rem",
            fontSize: "2rem",
            color: props.theme.palette.primary.contrastText,
            backgroundColor: props.theme.palette.secondary.light,
        },
        ".card-selected": {
            backgroundColor: props.theme.palette.secondary.main,
        },
    }
))

