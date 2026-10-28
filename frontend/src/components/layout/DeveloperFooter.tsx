import { FiCode, FiPhone, FiHeart } from "react-icons/fi";
export default function DeveloperFooter() {
  return (
    <footer className="developer-footer">
      <span>
        <FiCode aria-hidden="true" /> Developed by{" "}
        <strong>Virendra Arekar</strong>
      </span>
      <span className="footer-care">
        <FiHeart aria-hidden="true" /> Thoughtfully built for connected care
      </span>
      <a href="tel:8483988837">
        <FiPhone aria-hidden="true" />
        8483988837
      </a>
    </footer>
  );
}
