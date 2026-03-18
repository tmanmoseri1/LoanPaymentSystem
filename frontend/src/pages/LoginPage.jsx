import React, { useState } from 'react'
import api from '../lib/api.js'
import { setToken } from '../lib/auth.js'
import { Link, useNavigate } from 'react-router-dom'

export default function LoginPage() {
  const nav = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [msg, setMsg] = useState(null)

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setMsg(null)
    try {
      const res = await api.post('/auth/login', form)
      setToken(res.data.token)
      nav('/dashboard')
    } catch (err) {
      setMsg({ type: 'danger', text: err?.response?.data?.message || 'Login failed' })
    }
  }

  return (
    <div className="row justify-content-center">
      <div className="col-md-6">
        <h3>Login</h3>
        {msg && <div className={`alert alert-${msg.type}`}>{msg.text}</div>}
        <form onSubmit={submit} className="card card-body">
          <div className="mb-3">
            <label className="form-label">Username</label>
            <input className="form-control" name="username" value={form.username} onChange={onChange} required />
          </div>
          <div className="mb-3">
            <label className="form-label">Password</label>
            <input className="form-control" name="password" type="password" value={form.password} onChange={onChange} required />
          </div>
          <button className="btn btn-success">Login</button>
          <div className="mt-3">
            <span className="text-muted">No account?</span> <Link to="/register">Register</Link>
          </div>
          <div className="mt-3 small text-muted">
            Default admin: <code>admin</code> / <code>admin123</code> (created on startup)
          </div>
        </form>
      </div>
    </div>
  )
}
