const assert = require('assert');
const chaiHttp = require('chai-http');
const chai = require('chai');
const app = require('../main');
const should = chai.should();

chai.use(chaiHttp);

describe('Testing Hello', function()
{

  it('Validating hello message/body', (done) =>
  {
    chai.request(app).get('/hello').end((err, res) =>
    {
      //console.log(res.body);
      res.body.should.be.a('array');
      let json = res.body[0];
      assert.equal(true, json.message.includes("hello yourself"));
      done();
    });
  });

  it('Validating hello message/body length', (done) =>
  {
    chai.request(app).get('/hello').end((err, res) =>
    {
      //console.log(res.body);
      res.body.should.be.a('array');
      let json = res.body[0];
      assert.equal(true, json.message.length == 14);
      done();
    });
  });

  it('Validating hello responseCode', (done) =>
  {
    chai.request(app).get('/hello').end((err, res) =>
    {
      res.should.have.status(200);
      done();
    });
  });
});
