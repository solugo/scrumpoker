import {HTMLProps, useEffect, useState} from 'react';
import {Navigate, Route, Routes} from "react-router-dom";
import {Box, IconButton, styled, Toolbar, Tooltip, Typography} from "@mui/material";
import RoomRoute from "../routes/RoomRoute";
import {v4 as uuid} from 'uuid';
import Feedback from "../icons/Feedback";



const App = (props: HTMLProps<HTMLElement>) => {

    const [version, setVersion] = useState('');

    async function loadVersionInfo() {
        const response = await fetch("/api/version")
        const info = await response.json()
        setVersion(info['build.version'] ?? '')
    }

    useEffect(() => void loadVersionInfo(), [])

    function feedback() {
        window.open("https://github.com/solugo/scrumpoker/issues")
    }

    return (
        <div className={props.className}>
            <Toolbar className="toolbar">
                <Typography variant="h6" component="div">
                    Scrum Poker
                </Typography>
                <Typography className="version" variant="h6" component="div">
                    {version}
                </Typography>
                <Box sx={{flexGrow: 1}}/>
                <Tooltip title="Feedback">
                    <IconButton onClick={feedback} color="inherit">
                        <Feedback/>
                    </IconButton>
                </Tooltip>
            </Toolbar>
            <div className="content">
                <Routes>
                    <Route path="/" element={<Navigate to={`/rooms/${uuid()}`}/>}/>
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
            },
            '.version': {
                paddingLeft: '1rem',
                color: props.theme.palette.primary.dark,
            },
        }
    )
)