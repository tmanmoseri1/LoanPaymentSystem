import React, { useState } from 'react'
import api from '../lib/api.js'
import { Link, useNavigate } from 'react-router-dom'

export default function RegisterPage() {
  const nav = useNavigate()
  const [form, setForm] = useState({ firstName: '', lastName: '', username: '', password: '' })
  const [msg, setMsg] = useState(null)

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setMsg(null)
    try {
      await api.post('/auth/register', form)
      setMsg({ type: 'success', text: 'Registered! You can login now.' })
      setTimeout(() => nav('/login'), 800)
    } catch (err) {
      setMsg({ type: 'danger', text: err?.response?.data?.message || 'Registration failed' })
    }
  }

  return (
    <div className="row justify-content-center">
      <div className="col-md-6">
        <h3>Register</h3>
        {msg && <div className={`alert alert-${msg.type}`}>{msg.text}</div>}
        <form onSubmit={submit} className="card card-body">
          <div className="mb-3">
            <label className="form-label">First name</label>
            <input className="form-control" name="firstName" value={form.firstName} onChange={onChange} required />
          </div>
          <div className="mb-3">
            <label className="form-label">Last name</label>
            <input className="form-control" name="lastName" value={form.lastName} onChange={onChange} required />
          </div>
          <div className="mb-3">
            <label className="form-label">Username</label>
            <input className="form-control" name="username" value={form.username} onChange={onChange} required />
          </div>
          <div className="mb-3">
            <label className="form-label">Password</label>
            <input className="form-control" name="password" type="password" value={form.password} onChange={onChange} required />
          </div>
          <button className="btn btn-primary">Create account</button>
          <div className="mt-3">
            <span className="text-muted">Already have an account?</span> <Link to="/login">Login</Link>
          </div>
        </form>
      </div>
    </div>
  )
}
