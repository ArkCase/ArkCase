var winston = require(process.env['USERPROFILE'] + '/node_modules/winston');


winston.remove(winston.transports.Console);
winston.add(winston.transports.Console, { timestamp: true });
winston.add(winston.transports.File, { filename: 'winston-basic.log' });
module.exports = winston;

 