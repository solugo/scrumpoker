import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {createTheme, CssBaseline, ThemeProvider} from "@mui/material";
import {HashRouter} from "react-router-dom";
import App from "./components/App.tsx";

const theme = createTheme({
    palette: {
        primary: {
            main: "#0277bd",
            light: "#58a5f0",
            dark: "#004c8c",
        },
        secondary: {
            main: "#ff5722",
            light: "#ff8a50",
            dark: "#c41c00",
        },
    }
})

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <ThemeProvider theme={theme}>
            <CssBaseline>
                <HashRouter>
                    <App/>
                </HashRouter>
            </CssBaseline>
        </ThemeProvider>
    </StrictMode>,
)
