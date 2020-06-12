const assert = require('assert');
const chaiHttp = require('chai-http');
const chai = require('chai');
const app = require('../main');
const should = chai.should();

chai.use(chaiHttp);

describe('Testing Put Properties', function()
{

  it('Validating Put properties, correct values, id and api-key', (done) =>
  {
    chai.request(app).put('/properties/12')
      .set('api-key', 'cs4783FTW')
      .send({
        'city':'putTown',
        'zip':'00000009'
      })
      .end((err, res) =>
    {
      res.should.have.status(200);
      done();
    });
  });

  it('Validating Put properties, correct values, api-key and incorrect id', (done) =>
  {
    chai.request(app).put('/properties/0')
      .set('api-key', 'cs4783FTW')
      .send({
        'city':'putTown',
        'zip':'00000009'
      })
      .end((err, res) =>
    {
      res.should.have.status(404);
      done();
    });
  });

  it('Validating Put properties, correct values, api-key and invalid id', (done) =>
  {
    chai.request(app).put('/properties/a')
      .set('api-key', 'cs4783FTW')
      .send({
        'city':'putTown',
        'zip':'00000009'
      })
      .end((err, res) =>
    {
      res.should.have.status(400);
      done();
    });
  });

  it('Validating Put properties, correct values, id and incorrect api-key', (done) =>
  {
    chai.request(app).put('/properties/12')
      .set('api-key', 'something')
      .send({
        'city':'putTown',
        'zip':'00000009'
      })
      .end((err, res) =>
    {
      res.should.have.status(401);
      done();
    });
  });



});
