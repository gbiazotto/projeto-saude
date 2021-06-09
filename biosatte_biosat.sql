-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: 12-Jan-2020 às 21:21
-- Versão do servidor: 5.6.45-log
-- versão do PHP: 7.2.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `biosatte_biosat`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `cliente`
--

CREATE TABLE `cliente` (
  `id_cliente` int(11) NOT NULL,
  `nome_cliente` varchar(35) NOT NULL,
  `descricao` varchar(90) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Extraindo dados da tabela `cliente`
--

INSERT INTO `cliente` (`id_cliente`, `nome_cliente`, `descricao`) VALUES
(1, 'Biosat', 'Area de testes interna');

-- --------------------------------------------------------

--
-- Estrutura da tabela `login`
--

CREATE TABLE `login` (
  `id_login` int(11) NOT NULL,
  `nome_login` varchar(30) NOT NULL,
  `login_login` varchar(18) NOT NULL,
  `senha_login` varchar(18) NOT NULL,
  `data_login` date NOT NULL,
  `auditoria` varchar(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Extraindo dados da tabela `login`
--

INSERT INTO `login` (`id_login`, `nome_login`, `login_login`, `senha_login`, `data_login`, `auditoria`) VALUES
(1, 'Rogério Grijó Biazotto', 'gbiazotto', 'teste123', '2019-11-20', 'teste auditoria1');

-- --------------------------------------------------------

--
-- Estrutura da tabela `processo`
--

CREATE TABLE `processo` (
  `id_processo` int(11) NOT NULL,
  `hora_processo` datetime NOT NULL,
  `usuario_processo` int(11) NOT NULL,
  `id_pulseira` int(11) NOT NULL,
  `bpm` varchar(3) NOT NULL,
  `oximetria` varchar(3) NOT NULL,
  `pressaoa` varchar(3) NOT NULL,
  `pressaob` varchar(3) NOT NULL,
  `auditoria` varchar(18) NOT NULL,
  `latitude` varchar(12) NOT NULL,
  `longitude` varchar(12) NOT NULL,
  `status` varchar(1) NOT NULL,
  `id_cliente` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Extraindo dados da tabela `processo`
--

INSERT INTO `processo` (`id_processo`, `hora_processo`, `usuario_processo`, `id_pulseira`, `bpm`, `oximetria`, `pressaoa`, `pressaob`, `auditoria`, `latitude`, `longitude`, `status`, `id_cliente`) VALUES
(1, '2019-11-19 00:00:00', 1, 1, '99', '95', '102', '72', 'teste', '', '', '', 0);

-- --------------------------------------------------------

--
-- Estrutura da tabela `pulseira`
--

CREATE TABLE `pulseira` (
  `id_pulseira` int(11) NOT NULL,
  `modelo_pulseira` varchar(30) NOT NULL,
  `tipo_pulseira` varchar(3) NOT NULL,
  `ultimousuario_pulseira` int(11) NOT NULL,
  `auditoria_pulseira` varchar(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Extraindo dados da tabela `pulseira`
--

INSERT INTO `pulseira` (`id_pulseira`, `modelo_pulseira`, `tipo_pulseira`, `ultimousuario_pulseira`, `auditoria_pulseira`) VALUES
(1, 'pulseira teste 01', 'int', 1, 'teste auditoria1');

-- --------------------------------------------------------

--
-- Estrutura da tabela `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `nome_usuario` varchar(30) NOT NULL,
  `nascimento_usuario` date NOT NULL,
  `cpf_usuario` varchar(11) NOT NULL,
  `rg_usuario` varchar(10) NOT NULL,
  `endereco_usuario` varchar(30) NOT NULL,
  `tipo_usuario` varchar(3) NOT NULL,
  `auditoria` varchar(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Extraindo dados da tabela `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nome_usuario`, `nascimento_usuario`, `cpf_usuario`, `rg_usuario`, `endereco_usuario`, `tipo_usuario`, `auditoria`) VALUES
(1, 'Antonio Telles', '1978-11-05', '28745211123', '3295711722', 'Rua Amador Bueno, 74', 'xxx', 'teste');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id_cliente`);

--
-- Indexes for table `login`
--
ALTER TABLE `login`
  ADD PRIMARY KEY (`id_login`);

--
-- Indexes for table `processo`
--
ALTER TABLE `processo`
  ADD PRIMARY KEY (`id_processo`),
  ADD KEY `id_pulseira` (`id_pulseira`),
  ADD KEY `usuario_processo` (`usuario_processo`),
  ADD KEY `id_cliente` (`id_cliente`);

--
-- Indexes for table `pulseira`
--
ALTER TABLE `pulseira`
  ADD PRIMARY KEY (`id_pulseira`),
  ADD KEY `ultimousuario_pulseira` (`ultimousuario_pulseira`);

--
-- Indexes for table `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cliente`
--
ALTER TABLE `cliente`
  MODIFY `id_cliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `login`
--
ALTER TABLE `login`
  MODIFY `id_login` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `processo`
--
ALTER TABLE `processo`
  MODIFY `id_processo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `pulseira`
--
ALTER TABLE `pulseira`
  MODIFY `id_pulseira` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Limitadores para a tabela `processo`
--
ALTER TABLE `processo`
  ADD CONSTRAINT `processo_ibfk_1` FOREIGN KEY (`id_pulseira`) REFERENCES `pulseira` (`id_pulseira`),
  ADD CONSTRAINT `processo_ibfk_2` FOREIGN KEY (`usuario_processo`) REFERENCES `usuario` (`id_usuario`);

--
-- Limitadores para a tabela `pulseira`
--
ALTER TABLE `pulseira`
  ADD CONSTRAINT `pulseira_ibfk_1` FOREIGN KEY (`ultimousuario_pulseira`) REFERENCES `usuario` (`id_usuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
