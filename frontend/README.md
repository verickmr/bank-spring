# Conta Segura — Frontend

Interface web da API de contas bancárias, construída com React, TypeScript e Vite.

## Funcionalidades

- cadastro público para o primeiro acesso;
- login com JWT;
- consulta e abertura de contas corrente e poupança;
- depósitos e saques;
- aplicação de juros e rendimento;
- extrato de transações por conta.

## Executar localmente

Inicie primeiro a API em `http://localhost:8080`. Depois, a partir da raiz do repositório:

```bash
cd frontend
npm ci
npm run dev
```

Acesse `http://localhost:5173`. O Vite encaminha as requisições de `/api` para o backend durante o desenvolvimento.

No primeiro acesso com o banco vazio, use **Criar cadastro** na tela de login.

## Organização

```text
src/
├── components/ui/       componentes do shadcn/ui
├── features/            funcionalidades de autenticação, contas e cadastro
├── shared/api/          cliente HTTP e tratamento de erros
├── shared/components/   componentes reutilizados pela aplicação
├── shared/routing/      proteção das rotas autenticadas
└── shared/storage/      armazenamento do token JWT
```

Cada funcionalidade mantém seus tipos, validações, integração HTTP, hooks e componentes próximos. O TanStack Query controla o cache de dados do servidor; React Hook Form e Zod validam os formulários.

## Verificações

```bash
npm run typecheck
npm run lint
npm run build
```
