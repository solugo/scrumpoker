import React, { useState } from "react";
import {IconButton, styled} from "@mui/material";
import classNames from "classnames";
import { Cancel } from "@mui/icons-material";

type GameCardType = "SELECT" | "VIEW" | "DISABLED"

interface GameCardProps {
    className?: string
    selected: boolean
    type: GameCardType
    title?: string | undefined
    value?: string | null
    onClick?: () => void
    onClickCancel?: () => void
}

const GameCard = (props: GameCardProps) => {

    const cardClasses = classNames({
        "card": true,
        "card-active": props.type === "SELECT",
        "card-selected": props.selected,
    })

    const [showCancelButton, setShowCancelButton] = useState<'none' | 'block'>('none');

    return (
        <div className={props.className} onMouseEnter={() => setShowCancelButton('block')} onMouseLeave={() => setShowCancelButton('none')}>
            <div className="title">
                {props.title}
            </div>
            <div className={cardClasses} onClick={() => props.onClick?.()}>
                {props.onClickCancel && 
                    <IconButton className="cancel" aria-label="cancel" size="large" style={{display: showCancelButton}} onClick={() => props.onClickCancel?.()}>
                        <Cancel />
                    </IconButton>
                }
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
            aspectRatio: "3 / 4",
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
        ".cancel": {
            position: "absolute",
            top: "0",
            left: "0",
            padding: 0,
        },
    }
))

