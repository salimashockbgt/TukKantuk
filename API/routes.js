const {
    register,
    login,
    stories,
} = require('./handler');

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

module.exports = routes;