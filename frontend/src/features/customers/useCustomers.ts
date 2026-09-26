import { useQuery } from '@tanstack/react-query'
import { HttpError } from '../../shared/api/httpClient'
import { listCustomers } from './customers.api'
import type { Customer } from './customers.types'

export function useCustomers() {
  return useQuery<Customer[], HttpError>({
    queryKey: ['customers'],
    queryFn: listCustomers,
  })
}
