import {SessionState} from "./SessionState.ts";
import {SessionRequest} from "./SessionRequest.ts";

export type SessionApi = {
    readonly state: SessionState,
    readonly send: (event: SessionRequest) => void,
}
