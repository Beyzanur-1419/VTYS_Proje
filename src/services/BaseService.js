/**
 * Base service class with common CRUD operations
 */
class BaseService {
  constructor(model) {
    this.model = model;
  }

  async findById(id) {
    return this.model.findByPk(id);
  }

  async findAll(options = {}) {
    return this.model.findAll(options);
  }

  async findOne(options = {}) {
    return this.model.findOne(options);
  }

  async create(data) {
    return this.model.create(data);
  }

  async update(id, data) {
    const instance = await this.findById(id);
    if (!instance) throw new Error('Not found');
    return instance.update(data);
  }

  async delete(id) {
    const instance = await this.findById(id);
    if (!instance) throw new Error('Not found');
    return instance.destroy();
  }
}

module.exports = BaseService;