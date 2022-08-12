import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './components/App';
import {ThemeProvider} from "@emotion/react";
import {createTheme} from "@mui/material";
import {SessionScope} from "./components/SessionScope";
import {HashRouter} from "react-router-dom";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
)

const theme = createTheme({
    palette : {
        primary:  {
            main : "#0277bd",
            light: "#58a5f0",
            dark: "#004c8c",
        },
        secondary:  {
            main : "#ff5722",
            light : "#ff8a50",
            dark : "#c41c00",
        },
    }
})

root.render(
    <React.StrictMode>
        <ThemeProvider theme={theme}>
            <SessionScope>
                <HashRouter>
                    <App/>
                </HashRouter>
            </SessionScope>
        </ThemeProvider>
    </React.StrictMode>
)