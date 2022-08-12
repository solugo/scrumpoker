import {SessionConnection} from "./SessionConnection";
import {SessionRequest} from "./SessionRequest";

export class SessionApi {

    private readonly requestQueue : SessionRequest[] = []

    constructor(private connection: SessionConnection | undefined) {
    }

    joinRoom(roomId?: string) {
        this.connection?.send({
            type: 'joinRoom',
            roomId: roomId,
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