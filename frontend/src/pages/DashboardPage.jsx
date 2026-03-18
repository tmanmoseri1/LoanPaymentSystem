import React, { useEffect, useState } from 'react'
import api from '../lib/api.js'

export default function DashboardPage() {
  const [profile, setProfile] = useState(null)
  const [loans, setLoans] = useState([])
  const [selectedLoan, setSelectedLoan] = useState(null)
  const [payments, setPayments] = useState([])
  const [msg, setMsg] = useState(null)

  const [newLoan, setNewLoan] = useState({ amount: '1000.00', termMonths: 12 })
  const [pay, setPay] = useState({ amount: '100.00' })

  const loadDashboard = async () => {
    const res = await api.get('/dashboard')
    setProfile({ username: res.data.username, firstName: res.data.firstName, lastName: res.data.lastName })
    setLoans(res.data.loans || [])
  }

  const loadPayments = async (loanId) => {
    const res = await api.get(`/payments/loan/${loanId}`)
    setPayments(res.data || [])
  }

  useEffect(() => {
    loadDashboard().catch((e) => setMsg({ type: 'danger', text: e?.response?.data?.message || 'Failed to load dashboard' }))
  }, [])

  const createLoan = async (e) => {
    e.preventDefault()
    setMsg(null)
    try {
      await api.post('/loans', { amount: newLoan.amount, termMonths: Number(newLoan.termMonths) })
      await loadDashboard()
      setMsg({ type: 'success', text: 'Loan created' })
    } catch (e2) {
      setMsg({ type: 'danger', text: e2?.response?.data?.message || 'Failed to create loan' })
    }
  }

  const makePayment = async (e) => {
    e.preventDefault()
    if (!selectedLoan) return
    setMsg(null)
    try {
      await api.post('/payments', { loanId: selectedLoan.id, amount: pay.amount })
      await loadDashboard()
      await loadPayments(selectedLoan.id)
      // refresh selected loan object
      const updated = (await api.get(`/loans/${selectedLoan.id}`)).data
      setSelectedLoan(updated)
      setMsg({ type: 'success', text: 'Payment recorded' })
    } catch (e2) {
      setMsg({ type: 'danger', text: e2?.response?.data?.message || 'Failed to record payment' })
    }
  }

  return (
    <div>
      <h3>Dashboard</h3>

      {msg && <div className={`alert alert-${msg.type}`}>{msg.text}</div>}

      <div className="row g-3">
        <div className="col-md-5">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">My Profile</h5>
              {profile ? (
                <div>
                  <div><strong>User:</strong> {profile.username}</div>
                  <div><strong>Name:</strong> {profile.firstName} {profile.lastName}</div>
                </div>
              ) : (
                <div className="text-muted">Loading…</div>
              )}
            </div>
          </div>

          <div className="card mt-3">
            <div className="card-body">
              <h5 className="card-title">Create Loan</h5>
              <form onSubmit={createLoan}>
                <div className="mb-2">
                  <label className="form-label">Amount</label>
                  <input className="form-control" value={newLoan.amount}
                         onChange={(e) => setNewLoan({ ...newLoan, amount: e.target.value })} />
                </div>
                <div className="mb-2">
                  <label className="form-label">Term (months)</label>
                  <input className="form-control" type="number" min="1" value={newLoan.termMonths}
                         onChange={(e) => setNewLoan({ ...newLoan, termMonths: e.target.value })} />
                </div>
                <button className="btn btn-primary btn-sm">Create</button>
              </form>
            </div>
          </div>
        </div>

        <div className="col-md-7">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">My Loans</h5>
              {loans.length === 0 ? (
                <div className="text-muted">No loans yet.</div>
              ) : (
                <div className="table-responsive">
                  <table className="table table-sm align-middle">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Original</th>
                        <th>Remaining</th>
                        <th>Status</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      {loans.map(l => (
                        <tr key={l.id}>
                          <td className="small"><code>{String(l.id).slice(0, 8)}…</code></td>
                          <td>{l.originalAmount}</td>
                          <td>{l.remainingBalance}</td>
                          <td>
                            <span className={`badge ${l.status === 'SETTLED' ? 'text-bg-success' : 'text-bg-secondary'}`}>
                              {l.status}
                            </span>
                          </td>
                          <td className="text-end">
                            <button className="btn btn-outline-primary btn-sm" onClick={async () => {
                              setSelectedLoan(l)
                              await loadPayments(l.id)
                            }}>
                              View
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>

          {selectedLoan && (
            <div className="card mt-3">
              <div className="card-body">
                <h5 className="card-title">Loan Details</h5>
                <div className="small text-muted mb-2">Loan ID: <code>{selectedLoan.id}</code></div>
                <div className="row">
                  <div className="col-md-6">
                    <div><strong>Original:</strong> {selectedLoan.originalAmount}</div>
                    <div><strong>Remaining:</strong> {selectedLoan.remainingBalance}</div>
                    <div><strong>Status:</strong> {selectedLoan.status}</div>
                  </div>
                  <div className="col-md-6">
                    <h6>Make Payment</h6>
                    <form onSubmit={makePayment} className="d-flex gap-2">
                      <input className="form-control form-control-sm" value={pay.amount}
                        onChange={(e) => setPay({ amount: e.target.value })} />
                      <button className="btn btn-success btn-sm">Pay</button>
                    </form>
                  </div>
                </div>

                <hr />

                <h6>Payments</h6>
                {payments.length === 0 ? (
                  <div className="text-muted">No payments yet.</div>
                ) : (
                  <div className="table-responsive">
                    <table className="table table-sm">
                      <thead>
                        <tr>
                          <th>Amount</th>
                          <th>Date</th>
                        </tr>
                      </thead>
                      <tbody>
                        {payments.map(p => (
                          <tr key={p.id}>
                            <td>{p.amount}</td>
                            <td className="small text-muted">{new Date(p.createdAt).toLocaleString()}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          )}

        </div>
      </div>
    </div>
  )
}
