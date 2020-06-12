const assert = require('assert');
const chaiHttp = require('chai-http');
const chai = require('chai');
const app = require('../main');
const should = chai.should();

chai.use(chaiHttp);

describe('Testing Post Properties', function()
{

  it('Validating Post properties, correct api-key', (done) =>
  {
    chai.request(app).post('/properties')
      .set('api-key', 'cs4783FTW')
      .send({
        'address':"123 mochaTest",
        'city':'Testown',
        'state':'TS',
        'zip':'99999999'
      })
      .end((err, res) =>
    {
      res.should.have.status(200);
      done();
    });
  });

  it('Validating Post properties, incorrect api-key', (done) =>
  {
    chai.request(app).post('/properties')
      .set('api-key', 'something')
      .send({
        'address':"123 mochaTest",
        'city':'Testown',
        'state':'TS',
        'zip':'99999999'
      })
      .end((err, res) =>
    {
      res.should.have.status(401);
      done();
    });
  });


  it('Validating Post properties, incorrect values', (done) =>
  {
    chai.request(app).post('/properties')
      .set('api-key', 'cs4783FTW')
      .send({
        'address':"123 mochaTest",
        'city':'Testown',
        'state':'T',
        'zip':'99999999000000909'
      })
      .end((err, res) =>
    {
      res.should.have.status(400);
      done();
    });
  });


});
