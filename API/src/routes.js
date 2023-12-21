
import { register, login, stories, } from './handler.js'

const routes = [
    {
        method: 'POST',
        path: '/register',
        handler: register,
    },
    {
        method: 'POST',
        path: '/login',
        handler: login
    },
    {
        method: 'POST',
        path: '/stories',
        handler: stories
    }
];

export default routes;