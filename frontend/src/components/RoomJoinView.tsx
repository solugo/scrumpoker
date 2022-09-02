import {Button, Card, FormControl, InputLabel, MenuItem, Select, styled, TextField} from "@mui/material";
import React, {HTMLProps, useState} from "react";
import {useSessionApi} from "./SessionScope";
import {Room} from "../logic/Session";
import SelectInput from "@mui/material/Select/SelectInput";

interface RoomJoinViewProps {
    roomId: string
    className?: string
}

export default styled(
    (props: RoomJoinViewProps) => {
        const api = useSessionApi()
        const [name, setName] = useState<string>("")
        const [role, setRole] = useState<string>("PLAYER")

        function submit() {
            api?.joinRoom(name, role, props.roomId)
            return false
        }

        return (
            <form className={props.className} onSubmit={submit}>
                <TextField
                    fullWidth
                    variant="filled" label="Name" value={name}
                    onChange={(event) => setName(event.target.value)}
                />
                <TextField
                    fullWidth select
                    variant="filled" label="Role" value={role}
                    onChange={(event) => setRole(event.target.value)}
                >
                    <MenuItem value="PLAYER">Player</MenuItem>
                    <MenuItem value="SPECTATOR">Spectator</MenuItem>
                </TextField>
                <div>
                    <Button fullWidth type="submit" variant="contained">
                        Join
                    </Button>
                </div>
            </form>
        )
    }, {
        name: 'RoomJoinView'
    }
)(
    (props) => `
        display: flex;
        width: 20rem;
        flex-direction: column;
        gap: 2rem;
        
        margin: auto;
        padding: ${props.theme.spacing(2)};
    `
)