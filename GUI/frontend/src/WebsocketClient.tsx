import {Client} from "@stomp/stompjs";

const client: Client = new Client();
client.configure({
    brokerURL: 'ws://localhost:6060/ws',
    onStompError: (frame) => {
        console.log('Broker reported error: ' + frame.headers['message']);
        console.log('Additional details: ' + frame.body);
    },
    debug: (msg => console.log("Debug: ", msg))
});
client.activate();

export const useStompClient = () => {
    return client;
}