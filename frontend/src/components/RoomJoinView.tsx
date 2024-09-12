import {Button, MenuItem, styled, TextField} from "@mui/material";
import {FormEvent} from "react";
import {useStoredState} from "../hooks/UseStoredState";
import {SessionApi} from "../logic/SessionApi.ts";

interface RoomJoinViewProps {
    api: SessionApi,
    roomId: string
    className?: string
}

export default styled(
    (props: RoomJoinViewProps) => {
        const [name, setName] = useStoredState<string>("player_name", "")
        const [role, setRole] = useStoredState<string>("player_role", "PLAYER")
        const roomId = props.roomId

        function submit(event: FormEvent) {
            props.api.send({
                type: 'joinRoom',
                name,
                role,
                roomId,
            })
            event.preventDefault()
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