import React from "react";
import {styled} from "@mui/material";
import classNames from "classnames";

type GameCardType = "SELECT" | "VIEW" | "DISABLED"

interface GameCardProps {
    className?: string
    selected: boolean
    type: GameCardType
    title?: string | undefined
    value?: string | null
    onClick?: () => void
}

const GameCard = (props: GameCardProps) => {

    const cardClasses = classNames({
        "card": true,
        "card-active": props.type === "SELECT",
        "card-selected": props.selected,
    })

    return (
        <div className={props.className}>
            <div className="title">
                {props.title}
            </div>
            <div className={cardClasses} onClick={() => props.onClick?.()}>
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
            borderWidth: "0.1rem",
            borderRadius: "0.4rem",
            fontSize: "2rem",
            borderStyle: "dashed",
            color: props.theme.palette.primary.contrastText,
            borderColor: props.theme.palette.grey.A700,
            backgroundColor: "transparent",
        },
        ".card-active": {
            color: props.theme.palette.primary.contrastText,
            backgroundColor: props.theme.palette.grey.A700,
        },
        ".card-selected": {
            borderStyle: "solid",
            borderColor: props.theme.palette.secondary.dark,
            backgroundColor: props.theme.palette.secondary.main,
        },
        ".card-active:hover": {
            position: "relative",
            display: "grid",
            alignItems: "center",
            justifyContent: "center",
            borderRadius: "0.4rem",
            fontSize: "2rem",
            color: props.theme.palette.primary.contrastText,
            borderColor: props.theme.palette.secondary.main,
            backgroundColor: props.theme.palette.secondary.light,
        },
    }
))

