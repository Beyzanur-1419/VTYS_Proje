const mongoose = require('mongoose');
const config = require('./index');

const connectMongo = async () => {
  if (!config.MONGO_URI) {
    throw new Error('MONGODB_URI is not defined in environment variables');
  }

  await mongoose.connect(config.MONGO_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  });
  console.log('✅ MongoDB Connected Successfully');
};

module.exports = connectMongo;
