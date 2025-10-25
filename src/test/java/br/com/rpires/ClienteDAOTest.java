package br.com.rpires;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.rpires.dao.ClienteDAO;
import br.com.rpires.dao.IClienteDAO;
import br.com.rpires.dao.generic.jdbc.ConnectionFactory;
import br.com.rpires.dao.generic.jdbc.SchemaLoader;
import br.com.rpires.domain.Cliente;
import br.com.rpires.exceptions.DAOException;
import br.com.rpires.exceptions.MaisDeUmRegistroException;
import br.com.rpires.exceptions.TableException;
import br.com.rpires.exceptions.TipoChaveNaoEncontradaException;

import static org.junit.Assert.*;

public class ClienteDAOTest {

	private static IClienteDAO clienteDao;

	@BeforeClass
	public static void init() throws SQLException {
		try (Connection conn = ConnectionFactory.getConnection()) {

			SchemaLoader.criarSchema(conn);
		}
		clienteDao = new ClienteDAO();
	}

	@After
	public void limpaTabela() throws DAOException {
		Collection<Cliente> clientes = clienteDao.buscarTodos();
		for (Cliente c : clientes) {
			clienteDao.excluir(c.getCpf());
		}
	}

	private Cliente criarClienteExemplo(Long cpf, String nome) {
		Cliente c = new Cliente();
		c.setCpf(cpf);
		c.setNome(nome);
		c.setCidade("São Paulo");
		c.setEnd("Rua das Flores");
		c.setEstado("SP");
		c.setNumero(123);
		c.setTel(11999999999L);
		c.setEmail(nome.toLowerCase() + "@teste.com");
		return c;
	}

	@Test
	public void pesquisarCliente() throws MaisDeUmRegistroException, TableException, TipoChaveNaoEncontradaException, DAOException {
		Cliente cliente = criarClienteExemplo(12312312312L, "Rodrigo");
		clienteDao.cadastrar(cliente);

		Cliente consultado = clienteDao.consultar(cliente.getCpf());
		assertNotNull(consultado);
		assertEquals(cliente.getCpf(), consultado.getCpf());
	}

	@Test
	public void salvarCliente() throws TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException, DAOException {
		Cliente cliente = criarClienteExemplo(56565656565L, "Ana");
		Boolean retorno = clienteDao.cadastrar(cliente);
		assertTrue(retorno);

		Cliente consultado = clienteDao.consultar(cliente.getCpf());
		assertNotNull(consultado);
		assertEquals("Ana", consultado.getNome());
	}

	@Test
	public void excluirCliente() throws TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException, DAOException {
		Cliente cliente = criarClienteExemplo(44444444444L, "Carlos");
		clienteDao.cadastrar(cliente);

		Cliente consultado = clienteDao.consultar(cliente.getCpf());
		assertNotNull(consultado);

		clienteDao.excluir(cliente.getCpf());

		Cliente deletado = clienteDao.consultar(cliente.getCpf());
		assertNull(deletado);
	}

	@Test
	public void alterarCliente() throws TipoChaveNaoEncontradaException, MaisDeUmRegistroException, TableException, DAOException {
		Cliente cliente = criarClienteExemplo(33333333333L, "Julia");
		clienteDao.cadastrar(cliente);

		Cliente consultado = clienteDao.consultar(cliente.getCpf());
		assertNotNull(consultado);

		consultado.setNome("Julia Santos");
		clienteDao.alterar(consultado);

		Cliente alterado = clienteDao.consultar(consultado.getCpf());
		assertEquals("Julia Santos", alterado.getNome());
	}

	@Test
	public void buscarTodos() throws TipoChaveNaoEncontradaException, DAOException {
		Cliente c1 = criarClienteExemplo(11111111111L, "Rodrigo");
		Cliente c2 = criarClienteExemplo(22222222222L, "Alice");

		clienteDao.cadastrar(c1);
		clienteDao.cadastrar(c2);

		Collection<Cliente> lista = clienteDao.buscarTodos();
		assertEquals(2, lista.size());
	}

	@Test
	public void testInserirClienteComEmail() throws SQLException, TipoChaveNaoEncontradaException, DAOException, MaisDeUmRegistroException, TableException {
		Cliente cliente = criarClienteExemplo(12345678900L, "João da Silva");
		cliente.setEmail("joao@email.com");

		Boolean retorno = clienteDao.cadastrar(cliente);
		assertTrue(retorno);

		Cliente consultado = clienteDao.consultar(cliente.getCpf());
		assertNotNull(consultado);
		assertEquals("joao@email.com", consultado.getEmail());
	}

	@Test
	public void testInserirClienteComDataNascimento() throws SQLException, TipoChaveNaoEncontradaException, DAOException, MaisDeUmRegistroException, TableException {
		Cliente cliente = criarClienteExemplo(55555555555L, "Beatriz Lima");
		cliente.setEmail("bia@email.com");
		cliente.setDataNascimento(java.time.LocalDate.of(1995, 8, 20)); // <-- novo campo

		Boolean retorno = clienteDao.cadastrar(cliente);
		assertTrue(retorno);

		Cliente consultado = clienteDao.consultar(cliente.getCpf());
		assertNotNull(consultado);
		assertEquals(java.time.LocalDate.of(1995, 8, 20), consultado.getDataNascimento());
	}
}
