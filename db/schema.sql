CREATE TABLE IF NOT EXISTS correntista (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cpf VARCHAR(14) NOT NULL,
    nome VARCHAR(255),
    email VARCHAR(255),
    senha VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_correntista_cpf UNIQUE (cpf)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS conta (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(255),
    saldo DECIMAL(19,2) NOT NULL,
    correntista_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_conta_correntista
        FOREIGN KEY (correntista_id) REFERENCES correntista (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS conta_corrente (
    id BIGINT NOT NULL,
    limite DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_conta_corrente_conta
        FOREIGN KEY (id) REFERENCES conta (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS conta_poupanca (
    id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_conta_poupanca_conta
        FOREIGN KEY (id) REFERENCES conta (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    conta_id BIGINT,
    tipo VARCHAR(30),
    valor DECIMAL(19,2) NOT NULL,
    data DATETIME(6),
    descricao VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_transacao_conta
        FOREIGN KEY (conta_id) REFERENCES conta (id)
) ENGINE=InnoDB;

CREATE INDEX idx_conta_correntista ON conta (correntista_id);
CREATE INDEX idx_transacao_conta_data ON transacao (conta_id, data);
