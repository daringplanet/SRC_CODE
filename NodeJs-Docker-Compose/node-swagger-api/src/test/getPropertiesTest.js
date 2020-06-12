const assert = require('assert');
const chaiHttp = require('chai-http');
const chai = require('chai');
const app = require('../main');
const should = chai.should();

chai.use(chaiHttp);

describe('Testing Get Properties', function()
{

  it('Validating properties message/body fields', (done) =>
  {
    chai.request(app).get('/properties').end((err, res) =>
    {
      //console.log(res.body);
      res.body.should.be.a('array');
      let json = res.body[0];
      json.should.have.property('id');
      json.id.should.be.a('number');
      json.should.have.property('address');
      json.address.should.be.a('string');
      json.should.have.property('city');
      json.city.should.be.a('string');
      json.should.have.property('state');
      json.state.should.be.a('string');
      json.should.have.property('zip');
      json.zip.should.be.a('string');
      res.should.have.status(200);
      done();
    });
  });

});
