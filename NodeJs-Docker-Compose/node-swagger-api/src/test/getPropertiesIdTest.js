const assert = require('assert');
const chaiHttp = require('chai-http');
const chai = require('chai');
const app = require('../main');
const should = chai.should();

chai.use(chaiHttp);

describe('Testing Get Properties Id', function()
{

  it('Validating propertiesID, correct id.', (done) =>
  {
    //will never delete 12, will be used as a validater.
    chai.request(app).get('/properties/12')
    .end((err, res) =>
    {
      //console.log(res.body);
      res.should.be.json;
      res.body.should.be.a('object');
      let json = res.body;
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

  it('Validating propertiesID, incorrect id.', (done) =>
  {
    //will never delete 12, will be used as a validater.
    chai.request(app).get('/properties/0')
    .end((err, res) =>
    {
      res.should.have.status(404);
      done();
    });
  });

  it('Validating propertiesID, incorrect int', (done) =>
  {
    //will never delete 12, will be used as a validater.
    chai.request(app).get('/properties/a')
    .end((err, res) =>
    {
      res.should.have.status(400);
      done();
    });
  });


});
