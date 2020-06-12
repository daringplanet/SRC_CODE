const assert = require('assert');
const chaiHttp = require('chai-http');
const chai = require('chai');
const app = require('../main');
const should = chai.should();

chai.use(chaiHttp);

describe('Testing Delete Properties', function()
{

  it('Validating Delete properties, correct api-key and id', (done) =>
  {
    chai.request(app).get('/properties').end((err, res) =>
    {
      let json = res.body[res.body.length-1];
      let lastId = json.id;
      chai.request(app).delete('/properties/' + lastId)
      .set('api-key', 'cs4783FTW')
      .end((err, res) =>
      {
        res.should.have.status(200);
        done();
      });
    });
  });


  it('Validating Delete properties, correct id and incorrect api-key', (done) =>
  {
    chai.request(app).get('/properties').end((err, res) =>
    {
      let json = res.body[res.body.length-1];
      let lastId = json.id;
      chai.request(app).delete('/properties/' + lastId)
      .set('api-key', 'something')
      .end((err, res) =>
      {
        res.should.have.status(401);
        done();
      });
    });
  });

  it('Validating Delete properties, correct api-key, incorrect value', (done) =>
  {
      chai.request(app).delete('/properties/0')
      .set('api-key', 'cs4783FTW')
      .end((err, res) =>
      {
        res.should.have.status(404);
        done();
      });
  });

  it('Validating Delete properties, correct api-key, invalid value', (done) =>
  {
      chai.request(app).delete('/properties/a')
      .set('api-key', 'cs4783FTW')
      .end((err, res) =>
      {
        res.should.have.status(400);
        done();
      });
  });

});
