import {createContext, PropsWithChildren, useContext, useEffect, useMemo} from "react";
import {Session} from "../logic/Session";
import {SessionConnection} from "../logic/SessionConnection";
import {SessionApi} from "../logic/SessionApi";
import {useObservable} from "../util";

const SessionApiContext = createContext<{ api?: SessionApi }>({})
const SessionContext = createContext<{ session?: Session }>({})

export function useSessionApi(): SessionApi | undefined {
    return useContext(SessionApiContext)?.api
}

export function useSession(): Session | undefined {
    return useContext(SessionContext)?.session
}

export function SessionScope(props: PropsWithChildren) {
    const connection = useMemo(() => new SessionConnection(), [])
    const api = useMemo(() => new SessionApi(connection), [])
    const session = useObservable(connection.session$)?.session

    useEffect(() => connection.open(), [])

    return (
        <SessionApiContext.Provider value={{api}}>
            <SessionContext.Provider value={{session}}>
                {props.children}
            </SessionContext.Provider>
        </SessionApiContext.Provider>
    )
}