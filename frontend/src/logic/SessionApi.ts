import {SessionConnection} from "./SessionConnection";
import {SessionRequest} from "./SessionRequest";

export class SessionApi {

    private readonly requestQueue : SessionRequest[] = []

    constructor(private connection: SessionConnection | undefined) {
    }

    joinRoom(name: string, mode: string, roomId?: string) {
        this.connection?.send({
            type: 'joinRoom',
            roomId: roomId,
            name: name,
            mode: mode,
        })
    }

    resetRoom(roomId: string) {
        this.connection?.send({
            type: 'resetRoom',
            roomId: roomId,
        })
    }

    revealRoom(roomId: string, visible: boolean = true) {
        this.connection?.send({
            type: 'revealRoom',
            roomId: roomId,
            visible: visible,
        })
    }

    changeSelection(roomId: string, selection: string | null) {
        this.connection?.send({
            type: 'updateSelection',
            roomId: roomId,
            selection: selection,
        })
    }

}