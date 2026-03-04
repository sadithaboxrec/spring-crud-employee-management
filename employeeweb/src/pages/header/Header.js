import { Navbar, Container, Nav } from 'react-bootstrap';  // Correct import
import { Link } from 'react-router-dom';  // Correct import

import './Header.css';

const Header = () => {
  return (
    <Navbar bg="primary" variant="dark">
      <Container>
        <Navbar.Brand as={Link} to="/">Employee Management System</Navbar.Brand>
        <Nav className="ml-auto">
          <Nav.Link as={Link} to="/" className="nav-link">Employees</Nav.Link>
          <Nav.Link as={Link} to="/employee" className="nav-link">Post Employees</Nav.Link> 
        </Nav>
      </Container>
    </Navbar>
  );
};

export default Header;
