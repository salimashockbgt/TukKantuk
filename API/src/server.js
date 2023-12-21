import { server as _server } from '@hapi/hapi';
import routes from './routes.js';

const init = async () => {

    const server = _server({
        port: 9000,
        host: 'localhost'
    });

    server.route(routes);

    await server.start();
    console.log('Server running on %s', server.info.uri);
};

process.on('unhandledRejection', (err) => {

    console.log(err);
    process.exit(1);
});

init();