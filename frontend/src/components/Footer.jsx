import React from 'react';

const Footer = () => {
  return (
    <footer className="footer">
      <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '0.5rem', alignItems: 'center' }}>
        <p style={{ fontWeight: 600, color: 'var(--text-main)' }}>
          MediCare Portal &bull; Enterprise Healthcare &amp; Patient Management System
        </p>
        <p style={{ fontSize: '0.8rem' }}>
          Engineered with Spring Boot 3, Spring Security (JWT), MySQL, and React.
        </p>
      </div>
    </footer>
  );
};

export default Footer;
