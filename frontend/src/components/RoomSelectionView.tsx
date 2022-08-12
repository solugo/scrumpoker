import {Card, styled} from "@mui/material";
import React, {HTMLProps} from "react";

interface RoomSelectionViewProps extends HTMLProps<HTMLElement> {
    options: { [key: string]: number } | undefined
}

export default styled(
    (props: RoomSelectionViewProps) => {
        const options = Object.entries(props.options ?? {})

        return (
            <div className={props.className}>
                {options.map(([title, value]) => (
                    <Card className="card">
                        {title}
                    </Card>
                ))}
            </div>
        )
    }, {
        name: 'RoomSelectionView'
    }
)(
    (props) => `
        display: flex;
        padding: ${props.theme.spacing(2)};
        flex-wrap: wrap;
        gap: ${props.theme.spacing(2)};
        justify-content: center;
        
        .card {
            padding: ${props.theme.spacing(2)};
        }
    `
)