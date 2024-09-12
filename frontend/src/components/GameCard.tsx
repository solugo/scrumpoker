import {useState} from "react";
import {IconButton, lighten, styled} from "@mui/material";
import classNames from "classnames";
import MinusCircle from "../icons/MinusCircle";

interface GameCardProps {
    className?: string
    selected: boolean
    type: "SELECT" | "VIEW" | "DISABLED"
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
        <div className={props.className} onMouseEnter={() => setShowCancelButton('block')}
             onMouseLeave={() => setShowCancelButton('none')}>
            <div className="title">
                {props.title}
            </div>
            <div className={cardClasses} onClick={() => props.onClick?.()}>
                {props.onClickCancel &&
                    <IconButton
                        className="cancel"
                        aria-label="cancel"
                        size="large"
                        style={{display: showCancelButton}}
                        onClick={() => props.onClickCancel?.()}
                    >
                        <MinusCircle/>
                    </IconButton>
                }
                <span>{props.value}</span>
            </div>
        </div>
    )
}

export default styled(GameCard)((props) => {
    const deg = '145deg'
    const primaryStart = lighten(props.theme.palette.primary.main, 0.99)
    const primaryEnd = lighten(props.theme.palette.primary.main, 0.90)
    const secondaryStart = lighten(props.theme.palette.secondary.main, 0.99)
    const secondaryEnd = lighten(props.theme.palette.secondary.main, 0.90)
    const secondaryMain = props.theme.palette.secondary.main

    return {
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
            userSelect: "none",
            aspectRatio: "3 / 4",
            position: "relative",
            display: "grid",
            alignItems: "center",
            justifyContent: "center",
            borderWidth: "0.1rem",
            borderRadius: "0.4rem",
            fontSize: "2rem",
            borderStyle: props.type === 'SELECT' ? 'solid' : "dashed",
            color: props.theme.palette.primary.contrastText,
            borderColor: '#999',
            background: "transparent",
        },
        ".card-active": {
            color: props.theme.palette.text.primary,
            borderColor: '#BBB',
            boxShadow: "0 2px 4px 0 #CCC",
            background: `linear-gradient(${deg}, #EEE 0%, #DDD 100%)`,
        },
        ".card-selected": {
            boxShadow: "0 2px 4px 0 #CCC",
            borderStyle: "solid",
            color: props.theme.palette.text.primary,
            borderColor: props.theme.palette.primary.light,
            transform: props.type === 'SELECT' ? "translateY(-0.5rem)" : "none",
            background: `linear-gradient(${deg}, ${primaryStart} 0%, ${primaryEnd} 100%)`,
        },
        ".card-active:hover": {
            position: "relative",
            display: "grid",
            alignItems: "center",
            justifyContent: "center",
            borderRadius: "0.4rem",
            fontSize: "2rem",
            color: props.theme.palette.text.primary,
            borderColor: secondaryMain,
            background: `linear-gradient(${deg}, ${secondaryStart} 0%, ${secondaryEnd} 100%)`,
        },
        ".cancel": {
            position: "absolute",
            top: "0",
            left: "0",
            padding: 0,
        },
    }
})

