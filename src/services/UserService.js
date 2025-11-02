const BaseService = require('./BaseService');
const UserModel = require('../models/user.model');

class UserService extends BaseService {
  constructor() {
    super(UserModel);
  }

  async findByEmail(email) {
    return this.model.findOne({ where: { email } });
  }

  async comparePassword(user, password) {
    if (!user) return false;
    return user.comparePassword(password);
  }
}

module.exports = UserService;