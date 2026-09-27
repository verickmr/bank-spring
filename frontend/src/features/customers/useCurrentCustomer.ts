import { useQuery } from '@tanstack/react-query'
import { getCurrentCustomer } from './customers.api'

export function useCurrentCustomer() {
  return useQuery({
    queryKey: ['current-customer'],
    queryFn: getCurrentCustomer,
  })
}
