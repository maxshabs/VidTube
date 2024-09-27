import './CategoryButton.css';

function CategoryButton({category}) {
  return (
    <button className="category-button">{category}</button>
  );
}

export default CategoryButton;