import { request } from '../../shared/api/httpClient'
import type { CreateCustomerPayload, Customer } from './customers.types'

export function listCustomers() {
  return request<Customer[]>('/api/correntistas')
}

export function createCustomer(payload: CreateCustomerPayload) {
  return request<Customer>('/api/correntistas', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}
