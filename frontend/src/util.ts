import {Observable} from "rxjs";
import {useEffect, useState} from "react";
import {randomInt} from "crypto";

export function useObservable<T>(observable: Observable<T> | undefined): T | undefined {
    const [state, setState] = useState<T | undefined>(undefined)

    useEffect(() => {
        if (observable) {
            const subscription = observable.subscribe((value) => setState(value))
            return subscription.unsubscribe.bind(subscription)
        }
    }, [])

    return state
}