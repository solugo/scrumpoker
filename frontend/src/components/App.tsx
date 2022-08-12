import React, {HTMLProps} from 'react';
import {Link, Route, Routes, useNavigate} from "react-router-dom";
import {useSession} from "./SessionScope";
import {Box, Button, IconButton, styled, Toolbar, Typography} from "@mui/material";
import IndexRoute from "../routes/IndexRoute";
import RoomRoute from "../routes/RoomRoute";
import {v4 as uuid} from 'uuid';
import {Add} from "@mui/icons-material";

const App = (props: HTMLProps<HTMLElement>) => {
    const navigate = useNavigate()
    const session = useSession()

    function addRoom() {
        navigate(`/rooms/${uuid()}`, {})
    }

    return (
        <div className={props.className}>
            <Toolbar className="toolbar">
                <Typography variant="h6" component="div">
                    Scrum Poker
                </Typography>

                <Box sx={{flexGrow: 1}} />
                <IconButton onClick={addRoom} color="inherit"> <Add/> </IconButton>
            </Toolbar>
            <div className="content">
                <Routes>
                    <Route path="/" element={<IndexRoute/>}/>
                    <Route path="/rooms/:roomId" element={<RoomRoute/>}/>
                </Routes>
            </div>
        </div>
    )
}

export default styled(App)(
    (props) => (
        {
            width: '100vw',
            height: '100vh',
            display: 'grid',
            gridTemplateRows: '4em auto',
            '.toolbar': {
                color: props.theme.palette.primary.contrastText,
                backgroundColor: props.theme.palette.primary.main,
            },
            '.content': {
                display: "grid",
                backgroundColor: '#EEE',
            },
            '.room': {

                color: props.theme.palette.text.primary,
                backgroundColor: props.theme.palette.background.default,
            }
        }
    )
)