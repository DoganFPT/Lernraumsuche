const http = require('http');
const app = require('./app');

const port = 8088;

const server = http.createServer(app);

server.on('listening',function(){
    console.log('ok, server is running');
});

server.listen(port);
